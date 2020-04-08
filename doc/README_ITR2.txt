Build Instructions:
1) Minimum API level 26 (Google API) required for emulator
2) API 29 will not run properly due to introduction of power saving measures, we are investigating
    workarounds
3) Android Studio ver. 3.6.1 required to build project successfully
4) Java 8 is also required

App Runs with the following assumptions:
1) First run of the app is done with internet connection, otherwise how did the user get the app
    in the first place?
2) Subsequent runs of the app won't require internet connection, and has prescribed behaviour as
    per itr-2 user stories
3) App starts via UpdateActivity that checks for updates in the Surrey servers, this behaviour was
    permitted in Piazza:https://piazza.com/class/k47bhfajff66cx?cid=230

Scrum Roles for Iteration-2:
Product Owner: Daniel Olivera (dolivera)
Scrum Master: Sam Zhou (sszhou)
Repo Manager: Manavkumar jayeshbhai Patel (mjpatel)
Team Member: Hoang minh Nguyen (hmn4)