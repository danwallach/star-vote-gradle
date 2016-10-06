# STAR-Vote #

This is a copy of the [STAR-Vote](https://github.com/danwallach/STAR-Vote)
project as of 10/5/2016, updated to use Gradle. This does *not* include the
`web-server` subproject that was in the original repository, which uses the SBT
build system.

## Directions for IntelliJ IDEA ##
Open the repository root as a project, and everything should be imported in a
working state. You may need to manually refresh Gradle projects to resolve
necessary dependencies.

The application components of STAR-Vote may be run through IDEA or via the
Gradle tasks described below. If running through IDEA, supply the VM option
`-Djava.net.preferIPv4Stack=true`.

## Using the Gradle wrapper ##
Gradle can be used to build or run the project without loading IDEA. Use the 
Gradle wrapper `gradlew` (or a local installation of Gradle) to execute Gradle
tasks.

## Gradle tasks ##
The following Gradle tasks have been added to run the various components of 
STAR-Vote:

`runSupervisor`: Run the Supervisor application with serial number 0.
`runVotebox1`: Run the Votebox application with serial number 1.
`runVotebox2`: Run the Votebox application with serial number 2.
`runBallotScanner`: Run the legacy ballot scanner application with serial
number 3.

Note: there is no task supplied for running `Tap`. It should be run through
IDEA.

## Todo ##
- Fix JAR building in Gradle (currently breaks paths to external files, like
crypto certs/keys)
- Use repository dependencies instead of local JARS when possible
