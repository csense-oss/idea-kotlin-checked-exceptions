# Overview
A plugin that adds errors / hints / quickfix related to checked exceptions for kotlin.

The reason for this is that kotlin by it self, is an amazing language, but it have taken som actions regarding exceptions, especially that no exception is checked.

Although with alright reasoning why this is the case, this leaves a "hole" in which in java, exceptions are to be handled as they are "part" of an expected flow, which is to say, 
handling these exceptions are a lot more required than the "original" idea that we should avoid exceptions in regular code flows.

So for the more robust programming, we are to handle checked exceptions, but since either the compiler nor the IDEA IDE shows, even allows you to know about declared throws (from both java & kotlin), you are quite in a world of hurt when integrating with , say java, or people who uses exceptions.
So instead to force people into looking every exception up, this plugin is there to try and fill this hole.

Please be aware, this is NOT an encouragement to use exceptions everywhere and generally speaking going against the kotlin language design chooices, but to AID in the pratical difficulty that arrises when interacting with systems that have a given expectation that is simply not met, nor informed.
 

