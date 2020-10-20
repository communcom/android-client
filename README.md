# Commun Android client

## General

Commun is THE place for content and time that you want to monetise.
On Commun you can post and like any content you want in the communities of your choice. Whether you are an art lover, a pet enthusiast, or an aspiring poet, or just looking for fun, there is a way to earn for everyone on Commun. All you have to do is post and like, engage with what you like, and comment. These actions let you earn in the easiest way.
If you are a creator or an artist, bring your fans to Commun and get donations from them for your works. If you are a part of any offline community, especially charitable one, move your group to Commun and be rewarded for discussions and work you do. If you want to grow your own brand, start with Commun, set up your community, put on display your produce and get rewards and donations from the globe.

## Main

The application is written in compliance with the Clean Architecture principles and using the MVVM architectural pattern.
As the application does not store any data in the local database the logic of the application is based on receiving data from the server. All communications with the server are carried out through the commun4j library, under the hood of which there is interaction with the blockchain.
The application contains several types (flavors) of composing the project: prod, dev and checking, and two options for building (build types): debug, release. The main difference between assemblies is the server address to which all requests go.

## Used stack

Language - Kotlin
DI - Dagger 2
Database - N/A
Networking - Okhttp и commun4j
Asynchronous programming - Coroutines
