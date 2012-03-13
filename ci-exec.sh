#!/bin/bash -xe

GEMSET=bering
CI_RUBY=ruby-1.8.7-p334

export TMPDIR="`pwd`/tmp"
mkdir -p tmp

if [ -z $CI_RUBY ]; then
    echo "CI_RUBY must be set"
    exit 1
fi

set +xe
echo "Initializing RVM"
source ~/.rvm/scripts/rvm
set -xe

RVM_CONFIG="${CI_RUBY}@${GEMSET}"
set +xe
echo "Switching to ${RVM_CONFIG}"
rvm use $RVM_CONFIG
set -xe

which ruby
ruby -v

ruby install_gems.rb

buildr clean package