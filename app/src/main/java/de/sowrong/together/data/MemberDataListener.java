package de.sowrong.together.data;

import java.util.HashMap;

public interface MemberDataListener {
        public void onMemberDataChanged(HashMap<String, Member> members);
    }