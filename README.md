# "Clean" Refactor Notes
In July 2021 I did a major refactor. Here's what I did:
1. Migrate from Dagger to [Hilt](https://developer.android.com/training/dependency-injection/hilt-android).
1. Update [Navigation Component](https://developer.android.com/guide/navigation).
	- Now supports [multiple backstacks](https://medium.com/androiddevelopers/multiple-back-stacks-b714d974f134) by default.
1. Decoupling.
	- Avoid sharing viewmodels. It makes unit testing easier when I can test fragments in isolation.
1. Splitting business models into Entities and Dto's. This way I have a clear [business model](https://github.com/mitchtabian/Open-API-Android-App/blob/master/app/src/main/java/com/codingwithmitch/openapi/business/domain/models/BlogPost.kt), [network model](https://github.com/mitchtabian/Open-API-Android-App/blob/master/app/src/main/java/com/codingwithmitch/openapi/business/datasource/network/main/BlogPostDto.kt), and [caching model](https://github.com/mitchtabian/Open-API-Android-App/blob/master/app/src/main/java/com/codingwithmitch/openapi/business/datasource/cache/blog/BlogPostEntity.kt).
1. Writing use cases.
	- Unidirectional data flow with MVI and kotlin sealed classes. (See [Interactors](https://github.com/mitchtabian/Open-API-Android-App/tree/master/app/src/main/java/com/codingwithmitch/openapi/business/interactors))
1. Refactor message handling system to a [Queue](https://github.com/mitchtabian/Open-API-Android-App/blob/master/app/src/main/java/com/codingwithmitch/openapi/business/domain/util/Queue.kt).
1. Migrate from Shared Preferences to [DataStore](https://developer.android.com/topic/libraries/architecture/datastore).
1. Migrate from Kotlin synthetics to [ViewBinding](https://developer.android.com/topic/libraries/view-binding).
1. Write [Unit tests](https://github.com/mitchtabian/Open-API-Android-App/tree/master/app/src/test/java/com/codingwithmitch/openapi/interactors) for use-cases.

# TODO
1. Check out the new [splash screen APIs](https://developer.android.com/about/versions/12/features/splash-screen)
1. Do a [Compose](https://developer.android.com/jetpack/compose) refactor
	- I will create a new repo for this

# Test Account

email: `blake@tabian.ca`

password: `password`


<hr>

<a href='https://codingwithmitch.com/courses/powerful-android-apps-with-jetpack-architecture/' target='_blank'><img class='header-img' src='https://codingwithmitch.s3.amazonaws.com/static/powerful-android-apps-with-jetpack-architecture/images/powerful_android_apps_1.png' /></a>


<h1><a href="https://codingwithmitch.com/courses/powerful-android-apps-with-jetpack-architecture/">Powerful Android Apps with Jetpack Architecture</a></h1>
<p>Watch the video course here: <a href="https://codingwithmitch.com/courses/powerful-android-apps-with-jetpack-architecture/">Powerful Android Apps with Jetpack Architecture</a>.</p>

<p>In this course you'll learn to build a real application that interacts with the website <a href="https://open-api.xyz" target="_blank">open-api.xyz</a>.</p>
<p>Open-api.xyz is a sandbox website for codingwithmitch members to practice interacting with a Rest API. </p><br>

<h2><strong>Prerequisites</strong> (Recommended not required):</h2>
<ol>
<li>
<p><strong><a href="https://codingwithmitch.com/courses/dagger22-android/">Dagger 2</a></strong></p>
</li>

<li>
<p><strong><a href="https://codingwithmitch.com/courses/model-view-intent-mvi-architecture/">MVI Architecture</a></strong></p>
</li>

<li>
<p><strong><a href="https://codingwithmitch.com/courses/android-local-database-cache-rest-api/">Database Caching (Retrofit + Room)</a></strong></p>
</li>

</ol>
<br>

<h2><strong>What you'll learn:</strong></h2>
<ul>
<li><strong>Kotlin</strong>:</li>
<li>
<strong>Coroutines</strong>:<br>
<ol>
<li>Advanced coroutine management using jobs</li>
<li>Cancelling active jobs</li>
<li>Coroutine scoping</li>
</ol>
</li>
<li>
<strong>Navigation Components</strong>:<br>
<ol>
<li>Bottom Navigation View with fragments </li>
<li>Leveraging multiple navigation graphs (this is cutting edge content)</li>
</ol>
</li>
<li>
<strong>Dagger 2</strong>:<br>
<ol>
<li>custom scopes, fragment injection, activity injection, Viewmodel injection</li>
</ol>
</li>
<li>
<strong>MVI architecture</strong>:<br>
<ol>
<li>Basically this is MVVM with some additions</li>
<li>State management</li>
<li>Building a generic BaseViewModel</li>
<li>Repository pattern (NetworkBoundResource)</li>
</ol>
</li>
<li>
<strong>Room Persistence</strong>:<br>
<ol>
<li>SQLite on Android with Room Persistence library</li>
<li>Custom queries, inserts, deletes, updates</li>
<li>Foreign Key relationships</li>
<li>Multiple database tables</li>
</ol>
</li>
<li>
<strong>Cache</strong>:<br>
<ol>
<li>Database caching (saving data from network into local cache)</li>
<li>Single source of truth principal</li>
</ol>
</li>
<li>
<strong>Retrofit 2</strong>:<br>
<ol>
<li>Handling any type of response from server (success, error, none, etc...)</li>
<li>Returning LiveData from Retrofit calls (Retrofit Call Adapter)</li>
</ol>
</li>
<li>
<strong>ViewModels</strong>:<br>
<ol>
<li>Sharing a ViewModel between several fragments</li>
<li>Building a powerful generic BaseViewModel</li>
</ol>
</li>
<li>
<strong>WebViews</strong>:<br>
<ol>
<li>Interacting with the server through a webview (Javascript)</li>
</ol>
</li>
<li>
<strong>SearchView</strong>:<br>
<ol>
<li>Programmatically implement a SearchView</li>
<li>Execute search queries to network and db cache</li>
</ol>
</li>
<li>
<strong>Images</strong>:<br>
<ol>
<li>Selecting images from phone memory</li>
<li>Cropping images to a specific aspect ratio</li>
<li>Setting limitations on image size and aspect ratio</li>
<li>Uploading a cropped image to server</li>
</ol>
</li>
<li>
<strong>Network Request Management</strong>:<br>
<ol>
<li>Cancelling pending network requests (Kotlin coroutines)</li>
<li>Testing for network delays</li>
</ol>
</li>
<li>
<strong>Pagination</strong>:<br>
<ol>
<li>Paginating objects returned from server and database cache</li>
</ol>
</li>
<li>
<strong>Material Design</strong>:<br>
<ol>
<li>Bottom Navigation View with Fragments</li>
<li>Customizing Bottom Navigation Icon behavior</li>
<li>Handling Different Screen Sizes (ConstraintLayout)</li>
<li>Material Dialogs</li>
<li>Fragment transition animations</li>
</ol>
</li>
</ul>
<br>
