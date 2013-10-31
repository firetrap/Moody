Moody is a android open-source application for the [Moodle e-learning platform](https://moodle.org/about/).


This wiki is the main source of documentation for developers working with (or contributing to) moody/moodyRest projects. If this is your first time hearing about Moody, we recommend starting with the Moody [website](http://firetrap.github.io/Moody/).



<a name="how-it-works"/>
##How it works
1. The client sends a username and password to the web service login script.
1. The script returns a token for that user account.
1. The client calls a particular web service function on a protocol server including the token .
1. The protocol server uses the token to check that the user can call the function.
1. The protocol server call the matching external function, located in a externallib.php file inside the relevant module.
1. The external function checks that the current user has_capability to do this operation.
1. The external function calls the matching Moodle core function (in lib.php usually).
1. The core function can return a result to the external function.
1. The external function will return a result to the protocol server.
1. The protocol server returns the result to the client.


## What Moody can do
* Moody is able to get all the contents of your courses
* Notify you about new contents in your favorites
* Add your courses to your favorites
* Get the latest contents from all of your courses
* Search in your courses
* Send messages to contacts
* Edit profile picture


## First time with moody
If you want to see Moody on action before download, take a step in [First time with Moody](first-time-with-moody.md)


