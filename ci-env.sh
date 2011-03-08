##
# A script to set up the environment for running the tests in CI.
# This script should be sourced, not executed.

RUBY=ruby-1.8.7-p334
GEMSET=bering

set +x
. ~/.rvm/scripts/rvm
rvm use "${RUBY}@${GEMSET}"
set -x
ruby install_gems.rb
