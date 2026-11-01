package me.darkolythe.shulkerpacks;

/** 경험치와 입력 소모를 함께 확인하여 실패한 클릭에는 모루 손상을 적용하지 않습니다. */
record AnvilUse(boolean creative, int levelBefore, int cost) {
    boolean completed(int levelAfter, boolean firstInputConsumed) {
        return !creative && cost > 0 && firstInputConsumed && levelBefore - levelAfter >= cost;
    }
}
