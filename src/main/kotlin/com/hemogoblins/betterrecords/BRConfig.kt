package com.hemogoblins.betterrecords

import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue
import net.minecraftforge.common.ForgeConfigSpec.Builder
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue


object BRConfig {
    var CLIENT_SPEC: ForgeConfigSpec
    var COMMON_SPEC: ForgeConfigSpec
    var SERVER_SPEC: ForgeConfigSpec

    var CLIENT: Client
    var COMMON: Common
    var SERVER: Server

    class Client(builder: Builder) {

        val TESTVAL: BooleanValue


        init {
            builder.comment("Client config").push("client")

            this.TESTVAL = builder
                    .comment("Comment for testval")
                    .define("testval", false)

            builder.pop()
        }

    }


    class Common(builder: Builder) {

        init {

        }

    }

    class Server(builder: Builder) {

        init {

        }
    }

    init {
        val specPairClient: org.apache.commons.lang3.tuple.Pair<Client, ForgeConfigSpec> = ForgeConfigSpec.Builder().configure(::Client)
        CLIENT = specPairClient.left
        CLIENT_SPEC = specPairClient.right

        val specPairCommon: org.apache.commons.lang3.tuple.Pair<Common, ForgeConfigSpec> = ForgeConfigSpec.Builder().configure(::Common)
        COMMON = specPairCommon.left
        COMMON_SPEC = specPairCommon.right

        val specPairServer: org.apache.commons.lang3.tuple.Pair<Server, ForgeConfigSpec> = ForgeConfigSpec.Builder().configure(::Server)
        SERVER = specPairServer.left
        SERVER_SPEC = specPairServer.right

    }
}