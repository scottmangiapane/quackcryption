## QuackCryption

This app uses 128-bit AES and a bitmask to convert a string of text into an encrypted message with a funny twist. It was made as part of a hackathon project with the help of Michael Bilstein, Kevin Dong, and Vikram Pasumarti. It's "unquackable"!

## How to Use

To share encrypted text with someone else, be sure that both you and the other person have the same keys and init vectors (on the settings page). These values can be anything as long as they are a multiple of 16 characters long and identical on any devices you want to share data between. To encrypt text, type it in text box and click the duck icon. You can then send the encrypted data to anyone using any method (email, text, etc) knowing it will be secure. The other person can decrypt the text by pasting it into the text field on their app and clicking the duck icon.

## Download

https://play.google.com/store/apps/details?id=co.twoduck.quackcryption

## Screenshots

<img src="screenshots/1.png" width="200">
<img src="screenshots/2.png" width="200">
<img src="screenshots/3.png" width="200">

## Build Instructions

* Install the required tools
* Create a new project in Android Studio  
  Package name: co.twoduck.quackcryption
* Replace the contents of `/app/source/main/` with this repo
* Add any libraries to `/libs/`
* Add any dependencies to `/app/build.gradle`

## Build Requirements

* Android Studio
* Android Software Development Kit
* Java Development Kit

## Libraries

Add this library to the project's `/libs/` folder
* commons-codec-1.10.jar  
  https://commons.apache.org/proper/commons-codec/index.html

## Dependencies

In the project's `/app/build.gradle` file, add the following dependency.
```groovy
dependencies {
    compile 'com.android.support:appcompat-v7:24.1.1'
    compile 'com.android.support:cardview-v7:24.1.1'
    compile files('commons-codec-1.10.jar')
}
```
