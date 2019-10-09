package com.codingwithmitch.openapi.ui.main.blog

import android.os.Bundle
import android.view.*
import androidx.navigation.fragment.findNavController
import com.codingwithmitch.openapi.R
import kotlinx.android.synthetic.main.fragment_blog.*

class BlogFragment : BaseBlogFragment(){


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        goViewBlogFragment.setOnClickListener {
            findNavController().navigate(R.id.action_blogFragment_to_viewBlogFragment)
        }

    }



}

















