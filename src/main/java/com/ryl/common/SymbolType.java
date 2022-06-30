/*
 * Copyright (c) 2013 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ryl.common;

/**
 * Created by
 * User: hansolo
 * Date: 11.09.13
 * Time: 22:19
 */
public enum SymbolType {
    ALARM("alarm", 0.75, 0.8571428571428571),
    ALIGN_LEFT("align-left", 0.7857142857142857, 0.6428571428571429),
    ALIGN_RIGHT("align-right", 0.7857142857142857, 0.6428571428571429),
    ATTACHMENT("attachment", 0.9516587938581195, 0.8482142857142857),
    BACK("back", 0.5, 0.5),
    BIG_BRUSH("big-brush", 0.9289816447666713, 0.9220754759652274),
    BLUE_TOOTH("blue-tooth", 0.4839710848672049, 0.9076505388532367),
    BOOLEAN("boolean", 0.6428571428571429, 0.6428571428571429),
    BRIGHTNESS("brightness", 0.8571428571428571, 0.8571428571428571),
    BRUSH("brush", 0.961561747959682, 0.8979561669485909),
    BULB_ON("bulb-on", 0.9285714285714286, 0.9285714285714286),
    BULB("bulb", 0.6785714285714286, 0.9285714285714286),
    CALENDAR("calendar", 0.8928571428571429, 0.9285714285714286),
    CAMERA("camera", 0.9285714285714286, 0.75),
    CAR("car", 0.9285714285714286, 0.75),
    CART("cart", 0.861307007925851, 0.7142857142857143),
    CENTER("center", 0.7857142857142857, 0.6428571428571429),
    CLOCK("clock", 0.7857142857142857, 0.7857142857142857),
    CLOUD("cloud", 0.9285714285714286, 0.6428571428571429),
    COMPASS("compass", 0.7142857142857143, 0.8928571428571429),
    COMPOSE("compose", 0.8125, 0.8125),
    CONTRAST("contrast", 0.8571428571428571, 0.8571428571428571),
    CROP("crop", 0.7857142857142857, 0.7857142857142857),
    CURSOR("cursor", 0.6428571428571429, 1.0),
    CURSOR1("cursor1", 0.5657272338867188, 0.7659502710614886),
    CURSOR2("cursor2", 0.7931109837123326, 0.8209312983921596),
    DELETE("delete", 0.8571428571428571, 0.8571428571428571),
    EJECT("eject", 0.7142857142857143, 0.6339285714285714),
    ERASER("eraser", 0.8928571428571429, 0.7857142857142857),
    EYE("eye", 0.8214285714285714, 0.5714285714285714),
    EYEDROPPER("eye-dropper", 0.7142857142857143, 0.8571428571428571),
    FACEBOOK("facebook", 0.8571428571428571, 0.8571428571428571),
    FORWARD("forward", 0.6339285714285714, 0.5),
    GAUGE("gauge", 0.92857, 0.92857),
    GLOBE("globe", 0.7857142857142857, 0.7857142857142857),
    GOOGLE("google", 0.42857142857142855, 0.7142857142857143),
    GRAPH("graph", 0.9285714285714286, 0.7142857142857143),
    HEAD_PHONES("head-phones", 0.8571428571428571, 0.7857142857142857),
    HUMIDITY("humidity", 0.92857, 0.92857),
    INFO("info", 0.9285714286, 0.9285714286),
    INFO1("info1", 0.3928571429, 0.9285714286),
    HEART("heard", 0.8928571428571429, 0.7678571428571429),
    JUSTIFIED("justified", 0.7857142857142857, 0.6428571428571429),
    LAYERS("layers", 0.6428571428571429, 0.6428571428571429),
    LINE("line", 0.729079110281808, 0.729079110281808),
    LINK("link", 0.8571428571428571, 0.6428571428571429),
    LOCATION("location", 0.5357142857142857, 0.8392857142857143),
    LOCK("lock", 0.7142857142857143, 0.8571428571428571),
    LUGGAGE("luggage", 0.9285714285714286, 0.7142857142857143),
    MAIL("mail", 0.8571428571428571, 0.5357142857142857),
    MONITOR("monitor", 1.0, 0.8928571428571429),
    MULTI_RELAY("multi-relay", 0.85714, 0.92857),
    MUSIC("music", 0.75, 0.7498433249337333),
    NEXT("next", 0.5, 0.5),
    NONE("none", 1.0, 1.0),
    PAUSE("pause", 0.42857142857142855, 0.5),
    PEN("pen", 0.5312198911394391, 0.8392857142857143),
    PEN1("pen1", 0.9285714285714286, 0.9464285714285714),
    PENCIL("pencil", 0.9010003634861538, 0.9010004316057477),
    PHONE("phone", 0.7822575569152832, 0.7696306364876884),
    PHOTO("photo", 0.8571428571428571, 0.6428571428571429),
    PLANE("plane", 0.9285714285714286, 0.8878536224365234),
    PLAY("play", 0.41964285714285715, 0.5),
    POWER("power", 0.8571428571,0.9285714286),
    REFRESH("refresh", 0.7848343167986188, 0.75),
    RELAY("relay", 0.92857, 0.46429),
    REPEAT("repeat", 0.8571428571428571, 0.902665274483817),
    REWIND("rewind", 0.6339285714285714, 0.5),
    ROCKET("rocket", 0.75, 0.75),
    SCISSORS("scissors", 0.9196428571428571, 0.9196428571428571),
    SEARCH("search", 0.8214285714285714, 0.7857142857142857),
    SELECTION("selection", 0.7142857142857143, 0.7142857142857143),
    SELECTION1("selection1", 0.8571428571428571, 0.8571428571428571),
    SELECTION2("selection2", 0.7857142857142857, 0.6428571428571429),
    SELECTION3("selection3", 0.7857142857142857, 0.6428571428571429),
    SETTINGS("settings", 0.9285714285714286, 0.9285714285714286),
    SHUFFLE("shuffle", 0.9762328011648995, 0.7500634874616351),
    SMUDGE("smudge", 0.5714285714285714, 0.8392857142857143),
    SPEECH_BUBBLE("speech-bubble", 0.8571428571428571, 0.6428571428571429),
    STAR("star", 0.8571428571428571, 0.7946428571428571),
    TAG("tag", 0.8281781332833427, 0.8295073509216309),
    TAGS("tags", 0.9002538408551898, 0.936650208064488),
    TEMPERATURE("temperature", 0.4642857143, 0.9285714286),
    TEMPERATURE1("temperature1", 0.55, 0.92857),
    TEXT("text", 0.7142857142857143, 0.8571428571428571),
    TOOL("tool", 0.7767857142857143, 0.8125),
    TRAIN("train", 0.6428571428571429, 0.9285714285714286),
    TRASH("trash", 0.7142857142857143, 0.75),
    TWITTER("twitter", 0.8189540590558734, 0.739539555140904),
    UMBRELLA("umbrella", 0.8214285714, 0.7857142857),
    UNDO("undo", 0.7857142857142857, 0.7857142857142857),
    UNLOCK("unlock", 0.9285714285714286, 0.8571428571428571),
    USER("user", 0.7857142857142857, 0.7321428571428571),
    VOLUME("volume", 0.8571428571428571, 0.769810676574707),
    VOLTAGE("voltage", 0.4642857143, 0.9285714286),
    WEB("web", 0.7142857142857143, 0.75),
    ZOOM_IN("zoom-in", 0.8214285714285714, 0.7857142857142857),
    ZOOM_OUT("zoom-out", 0.8214285714285714, 0.7857142857142857);

    public final String STYLE_CLASS;
    public final double WIDTH_FACTOR;
    public final double HEIGHT_FACTOR;


    private SymbolType(final String STYLE_CLASS, final double WIDTH_FACTOR, final double HEIGHT_FACTOR) {
        this.STYLE_CLASS   = STYLE_CLASS;
        this.WIDTH_FACTOR  = WIDTH_FACTOR;
        this.HEIGHT_FACTOR = HEIGHT_FACTOR;
    }
}
