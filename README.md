<img width="36%%" alt="Mapbox" src="https://github.com/Ibrahim-Mushtaha/Mapbox-App/blob/master/app/src/main/res/drawable/logo.png" style="max-width:100%;"></a><br><br>
The Mapbox Navigation SDK is a precise and flexible platform which enables your users to explore the world's streets. We are designing new maps specifically for navigation that highlight traffic conditions and helpful landmarks. The calculations use the user's current location and compare it to the current route that the user's traversing to provide critical information at any given moment. You control the entire experience, from the time your user chooses a destination to when they arrive.


## ✨ Features:
<ul>
<li>100% Kotlin</li>
<li>MVVM architecture</li>
<li>Android architecture components</li>
<li>Navigation Jetpack</li>
<li>Single activity</li>
<li>DataBinding</li>
<li>Coroutines</li>
<li>Mapbox Direction</li>
<li>OpenCelliD</li>
</ul>


<h2><a id="user-content-soon-new-features-and-bugs-will-be-fixed-on-the-next-update-very-soon-" class="anchor" aria-hidden="true" href="#soon-new-features-and-bugs-will-be-fixed-on-the-next-update-very-soon-"><svg class="octicon octicon-link" viewBox="0 0 16 16" version="1.1" width="16" height="16" aria-hidden="true"><path fill-rule="evenodd" d="M7.775 3.275a.75.75 0 001.06 1.06l1.25-1.25a2 2 0 112.83 2.83l-2.5 2.5a2 2 0 01-2.83 0 .75.75 0 00-1.06 1.06 3.5 3.5 0 004.95 0l2.5-2.5a3.5 3.5 0 00-4.95-4.95l-1.25 1.25zm-4.69 9.64a2 2 0 010-2.83l2.5-2.5a2 2 0 012.83 0 .75.75 0 001.06-1.06 3.5 3.5 0 00-4.95 0l-2.5 2.5a3.5 3.5 0 004.95 4.95l1.25-1.25a.75.75 0 00-1.06-1.06l-1.25 1.25a2 2 0 01-2.83 0z"></path></svg></a><g-emoji class="g-emoji" alias="soon" fallback-src="https://github.githubassets.com/images/icons/emoji/unicode/1f51c.png">📱</g-emoji> Sample App
</h2>

![gif (2)](https://user-images.githubusercontent.com/63853553/103372155-f002b500-4ad9-11eb-977e-df74ca2136d1.gif)<br>

## 🕹 Getting Started:
You will need to provide developer access tokens to fetch the data from Mapbox.

<ul>
<li>Generate a new access token from <a href="https://account.mapbox.com/access-tokens/" rel="nofollow">here</a>. Copy the key and go back to Android project.</li>
<li>Define a constant <code>API_KEY</code> with the double quotes, it looks like</li><br>
<pre><p> 
   Mapbox.getInstance(requireActivity(),requireActivity().getString(R.string.tokenKey))
</p></pre>
<li>Add Mapbox Dependency</li><br>
<pre><p><span>dependencies</span> {
implementation <span><span>'</span>com.mapbox.mapboxsdk:mapbox-android-sdk:9.2.0'</span>
implementation <span><span>'</span>com.mapbox.mapboxsdk:mapbox-android-navigation:0.42.6'</span>
implementation <span><span>'</span>com.mapbox.mapboxsdk:mapbox-android-plugin-places-v9:0.12.0'</span>
}</p></pre>
</ul>
