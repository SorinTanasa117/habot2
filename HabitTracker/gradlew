#!/usr/bin/env sh

#
# Copyright 2015-present, Gradle, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# We need to be able to find the TT_HOME, from which we can find the rest of the tools.
if [ -z "$TT_HOME" ] ; then
    # If TT_HOME is not set, we are probably running in a development environment.
    # We can find the tools from the location of this script.
    if [ -L "$0" ] ; then
        # If the script is a symlink, we need to find the real path.
        script_path=$(readlink "$0")
    else
        script_path="$0"
    fi
    TT_HOME=$(cd "$(dirname "$script_path")"/.. && pwd)
    export TT_HOME
fi

# We need to be able to find the java executable.
if [ -z "$JAVA_HOME" ] ; then
    # If JAVA_HOME is not set, we will try to find it.
    if [ -x "/usr/libexec/java_home" ] ; then
        JAVA_HOME=$(/usr/libexec/java_home)
        export JAVA_HOME
    else
        # If we can't find it, we will just assume it is in the path.
        JAVA_HOME=$(dirname $(dirname $(which java)))
        export JAVA_HOME
    fi
fi

# We need to be able to find the gradle executable.
if [ -z "$GRADLE_HOME" ] ; then
    # If GRADLE_HOME is not set, we will try to find it.
    if [ -x "$TT_HOME/gradle/bin/gradle" ] ; then
        GRADLE_HOME="$TT_HOME/gradle"
        export GRADLE_HOME
    else
        # If we can't find it, we will just assume it is in the path.
        GRADLE_HOME=$(dirname $(dirname $(which gradle)))
        export GRADLE_HOME
    fi
fi

# Now we can run gradle.
"$GRADLE_HOME/bin/gradle" "$@"
