package de.sowrong.together.data;

import java.util.HashMap;

public interface CleaningDataListener {
        public void onCleaningDataChanged(HashMap<String, CleaningWeek> cleaning);
        public void onDutyDataChanged(HashMap<String, Duty> duties);
    }