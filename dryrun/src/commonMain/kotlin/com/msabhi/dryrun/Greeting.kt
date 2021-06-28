package com.msabhi.dryrun


class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}
