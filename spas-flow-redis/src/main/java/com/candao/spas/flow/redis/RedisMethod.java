package com.candao.spas.flow.redis;

public enum RedisMethod {
    ExistsKey("existsKey") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    KeyExpression("keyExpression"),
    GetKeys("getKeys"),
    DelKey("delKey"),
    DelKeyAndFlagKey("delKeyAndFlagKey") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    SetKeyExpireTime("setKeyExpireTime") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    GetKeyExpireTime("getKeyExpireTime") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    GetValue("getValue") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    GetValues("getValues") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    SetValue("setValue") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    Setnx("setnx") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    AddList("addList") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    AddListToHead("addListToHead") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    SetListElement("setListElement") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    GetListRange("getListRange") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    GetListFirstElement("getListFirstElement") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    GetListPop("getListPop") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    GetListPopOfLast("getListPopOfLast") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    GetListLastElement("getListLastElement") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    GetListSize("getListSize") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    RemoveValueFromList("removeValueFromList") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    SAdd("sAdd") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    SSize("sSize") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    SSet("sSet") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    SRemove("sRemove") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    SExists("sExists") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    AddToHashMap("addToHashMap") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    GetValueFromHashMap("getValueFromHashMap") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    GetValuesFromHashMap("getValuesFromHashMap") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    GetAllFromHashMap("getAllFromHashMap") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    GetSizeFromHashMap("getSizeFromHashMap") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    Type("type") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    RemoveFromHashMap("removeFromHashMap") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    GetKeysFromHashMap("getKeysFromHashMap") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    HasKeyFromHashMap("hasKeyFromHashMap") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    Incr("incr") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    Decr("decr") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    // =========SortedSet==============
    Zadd("zadd") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    Zrem("zrem") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    Zrange("zrange") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    ZrangeAll("zrangeAll") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    Zrevrange("zrevrange") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    ZrevrangeAll("zrevrangeAll") {
        @Override
        public boolean needLog() {
            return false;
        }
    },
    ;
    private String methodName;

    private RedisMethod(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return this.methodName;
    }

    /**
     * 是否需要记录日志
     *
     * @return
     */
    public boolean needLog() {
        return true;
    }
}