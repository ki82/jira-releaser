Jira Releaser
=========================

A Tool for setting fix version in Jira for issues with last changes before a certain revision.
A server running fishEye that is connected to Jira is needed for parsing the issues.

If you would like to extend the Jira Releaser you could create an separate maven project and call
MainApplication.runWithArgs() from your application.

Known Limitations
-----------------
Currently only works if the SCM repository is Subversion.
Currently only works if the checkins are on trunk.

Change Log
----------

### 0.0.5 (23 May 2012)
- Added support for unlimited number of Jira issues.

### 0.0.4 (15 May 2012)
- Added startDate as optional parameter

### 0.0.3 (10 May 2012)
- Added extension point MainApplication.runWithArgs()

### 0.0.2 (9 May 2012)
- Updated maven repo URL:s

### 0.0.1 (9 May 2012)
- Initial release