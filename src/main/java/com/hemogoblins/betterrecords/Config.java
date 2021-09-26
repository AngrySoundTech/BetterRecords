package com.hemogoblins.betterrecords;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config {

    public static final Client CLIENT;
    public static final Common COMMON;

    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        final Pair<Client, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(Client::new);
        final Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);

        CLIENT = clientSpecPair.getLeft();
        COMMON = commonSpecPair.getLeft();
        CLIENT_SPEC = clientSpecPair.getRight();
        COMMON_SPEC = commonSpecPair.getRight();
    }

    public static class Client {

        public Client(ForgeConfigSpec.Builder builder) {
            // TODO
        }
    }

    public static class Common {

        public Common(ForgeConfigSpec.Builder builder) {
            // TODO
        }
    }
}
