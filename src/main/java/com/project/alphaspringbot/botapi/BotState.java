package com.project.alphaspringbot.botapi;

public enum BotState {
    START, START_AGE_START, START_AGE_END, START_SEX_START, START_WEIGHT_START, START_SEX_WOMAN, START_SEX_MAN, START_WEIGHT_END,
    START_GROWTH_START, START_GROWTH_END, START_SPORT_START, START_SPORT_END, START_CALCULATE, BODY_SEX_WOMAN,
    PROCESSING,
    EAT_START, EAT_NAME, EAT_CALORIES, EAT_FATS, EAT_PROTEINS, EAT_CARBOHYDRATES, EAT_WEIGHT, EAT_CLOSE, EAT_YESANSWER, EAT_NOANSWER,
    PUKE_START, PUKE_INPUT, PUKE_DELETE,
    CALORIES, CALORIES_START, CALORIES_STOP,
    INFO, INFO_TOTAL, INFO_TODAY,
    HELP,
    BODY, BODY_WEIGHT_START, BODY_GROWTH_START, BODY_SPORT_START, BODY_WEIGHT_END, BODY_GROWTH_END, BODY_SPORT_END, BODY_CALCULATE,
    BODY_AGE_START, BODY_AGE_END, BODY_SEX_START, BODY_SEX_MAN
}
