package com.codingwithmitch.openapi.presentation.main.blog.list

import android.app.SearchManager
import android.content.Context.SEARCH_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.codingwithmitch.openapi.R
import com.codingwithmitch.openapi.business.datasource.cache.blog.BlogQueryUtils.Companion.BLOG_FILTER_DATE_UPDATED
import com.codingwithmitch.openapi.business.datasource.cache.blog.BlogQueryUtils.Companion.BLOG_FILTER_USERNAME
import com.codingwithmitch.openapi.business.datasource.cache.blog.BlogQueryUtils.Companion.BLOG_ORDER_ASC
import com.codingwithmitch.openapi.business.datasource.cache.blog.BlogQueryUtils.Companion.BLOG_ORDER_DESC
import com.codingwithmitch.openapi.business.domain.models.BlogPost
import com.codingwithmitch.openapi.business.domain.util.*
import com.codingwithmitch.openapi.databinding.FragmentBlogBinding
import com.codingwithmitch.openapi.presentation.main.blog.BaseBlogFragment
import com.codingwithmitch.openapi.presentation.util.TopSpacingItemDecoration
import com.codingwithmitch.openapi.presentation.util.processQueue
import kotlinx.coroutines.*

class BlogFragment : BaseBlogFragment(),
    BlogListAdapter.Interaction,
    SwipeRefreshLayout.OnRefreshListener
{

    private lateinit var searchView: SearchView
    private var recyclerAdapter: BlogListAdapter? = null // can leak memory so need to null
    private val viewModel: BlogViewModel by viewModels()
    private lateinit var menu: Menu

    private var _binding: FragmentBlogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBlogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        binding.swipeRefresh.setOnRefreshListener(this)
        initRecyclerView()
        subscribeObservers()
    }

    private fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner, { state ->

            uiCommunicationListener.displayProgressBar(state.isLoading)

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object: StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(BlogEvents.OnRemoveHeadFromQueue)
                    }
                })

            recyclerAdapter?.apply {
                submitList(blogList = state.blogList)
            }
        })
    }

    private fun initSearchView(){
        activity?.apply {
            val searchManager: SearchManager = getSystemService(SEARCH_SERVICE) as SearchManager
            searchView = menu.findItem(R.id.action_search).actionView as SearchView
            searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            searchView.maxWidth = Integer.MAX_VALUE
            searchView.setIconifiedByDefault(true)
            searchView.isSubmitButtonEnabled = true
        }

        // ENTER ON COMPUTER KEYBOARD OR ARROW ON VIRTUAL KEYBOARD
        val searchPlate = searchView.findViewById(R.id.search_src_text) as EditText

        // set initial value of query text after rotation/navigation
        viewModel.state.value?.let { state ->
            if(state.query.isNotBlank()){
                searchPlate.setText(state.query)
                searchView.isIconified = false
                binding.focusableView.requestFocus()
            }
        }
        searchPlate.setOnEditorActionListener { v, actionId, event ->

            if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED
                || actionId == EditorInfo.IME_ACTION_SEARCH ) {
                val searchQuery = v.text.toString()
                Log.e(TAG, "SearchView: (keyboard or arrow) executing search...: ${searchQuery}")
                executeNewQuery(searchQuery)
            }
            true
        }

        // SEARCH BUTTON CLICKED (in toolbar)
        val searchButton = searchView.findViewById(R.id.search_go_btn) as View
        searchButton.setOnClickListener {
            val searchQuery = searchPlate.text.toString()
            Log.e(TAG, "SearchView: (button) executing search...: ${searchQuery}")
            executeNewQuery(searchQuery)
        }

    }

    private fun executeNewQuery(query: String){
        viewModel.onTriggerEvent(BlogEvents.UpdateQuery(query))
        viewModel.onTriggerEvent(BlogEvents.NewSearch)
        resetUI()
    }

    private  fun resetUI(){
        uiCommunicationListener.hideSoftKeyboard()
        binding.focusableView.requestFocus()
    }

    private fun initRecyclerView(){
        binding.blogPostRecyclerview.apply {
            layoutManager = LinearLayoutManager(this@BlogFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(30)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerAdapter = BlogListAdapter(this@BlogFragment)
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    Log.d(TAG, "onScrollStateChanged: exhausted? ${viewModel.state.value?.isQueryExhausted}")
                    if (
                        lastPosition == recyclerAdapter?.itemCount?.minus(1)
                        && viewModel.state.value?.isLoading == false
                        && viewModel.state.value?.isQueryExhausted == false
                    ) {
                        Log.d(TAG, "BlogFragment: attempting to load next page...")
                        viewModel.onTriggerEvent(BlogEvents.NextPage)
                    }
                }
            })
            adapter = recyclerAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu
        inflater.inflate(R.menu.search_menu, this.menu)
        initSearchView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_filter_settings -> {
                showFilterDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemSelected(position: Int, item: BlogPost) {
        try{
            viewModel.state.value?.let { state ->
                    val bundle = bundleOf("blogPostPk" to item.pk)
                    findNavController().navigate(R.id.action_blogFragment_to_viewBlogFragment, bundle)
            }?: throw Exception("Null BlogPost")
        }catch (e: Exception){
            e.printStackTrace()
            viewModel.onTriggerEvent(
                BlogEvents.Error(
                stateMessage = StateMessage(
                    response = Response(
                        message = e.message,
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Error()
                    )
                )
            ))
        }
    }

    override fun onRefresh() {
        viewModel.onTriggerEvent(BlogEvents.NewSearch)
        binding.swipeRefresh.isRefreshing = false
    }

    fun showFilterDialog(){
        activity?.let {
            viewModel.state.value?.let { state ->
                val filter = state.filter.value
                val order = state.order.value

                val dialog = MaterialDialog(it)
                    .noAutoDismiss()
                    .customView(R.layout.layout_blog_filter)

                val view = dialog.getCustomView()

                view.findViewById<RadioGroup>(R.id.filter_group).apply {
                    when (filter) {
                        BLOG_FILTER_DATE_UPDATED -> check(R.id.filter_date)
                        BLOG_FILTER_USERNAME -> check(R.id.filter_author)
                    }
                }

                view.findViewById<RadioGroup>(R.id.order_group).apply {
                    when (order) {
                        BLOG_ORDER_ASC -> check(R.id.filter_asc)
                        BLOG_ORDER_DESC -> check(R.id.filter_desc)
                    }
                }

                view.findViewById<TextView>(R.id.positive_button).setOnClickListener {
                    val newFilter =
                        when (view.findViewById<RadioGroup>(R.id.filter_group).checkedRadioButtonId) {
                            R.id.filter_author -> BLOG_FILTER_USERNAME
                            R.id.filter_date -> BLOG_FILTER_DATE_UPDATED
                            else -> BLOG_FILTER_DATE_UPDATED
                        }

                    val newOrder =
                        when (view.findViewById<RadioGroup>(R.id.order_group).checkedRadioButtonId) {
                            R.id.filter_desc -> "-"
                            else -> ""
                        }

                    viewModel.apply {
                        onTriggerEvent(BlogEvents.UpdateFilter(getFilterFromValue(newFilter)))
                        onTriggerEvent(BlogEvents.UpdateOrder(getOrderFromValue(newOrder)))
                        onTriggerEvent(BlogEvents.NewSearch)
                    }

                    dialog.dismiss()
                }

                view.findViewById<TextView>(R.id.negative_button).setOnClickListener {
                    Log.d(TAG, "FilterDialog: cancelling filter.")
                    dialog.dismiss()
                }

                dialog.show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerAdapter = null
        _binding = null
    }
}








