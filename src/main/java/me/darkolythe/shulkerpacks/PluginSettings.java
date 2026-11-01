package me.darkolythe.shulkerpacks;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** 플러그인에 내장된 설정입니다. 값을 변경한 뒤 다시 빌드하여 적용합니다. */
public enum PluginSettings {
    INSTANCE;

    // 제작대 아이템을 우클릭하여 휴대용 제작 창을 엽니다.
    final boolean portableCrafting = true;
    final boolean portableEnderChest = true;
    final boolean portableStonecutter = true;
    final boolean portableLoom = true;
    final boolean portableGrindstone = true;
    final boolean portableAnvil = true;
    // 바닐라 모루의 작업 1회당 손상 확률입니다.
    final double anvilDamageChance = 0.12;

    // 이름에 다음 문자열이 포함된 인벤토리에서는 열기를 차단합니다.
    final List<String> blacklist = Collections.unmodifiableList(Arrays.asList(
            "&4&lMineXChange", "test inventory", " Contents", "Auction", "Preview",
            "DeathSign", " Item", "&cTrade with", "Editing", "Grave"));

    final boolean canopeninair = true;
    final boolean canopenininventory = true;
    final boolean canopeninvirtualinventory = true;
    final boolean canopeninchests = true;
    final boolean canopeninenderchest = true;
    final boolean canopeninbarrels = true;
    final boolean canplaceshulker = true;

    // true이면 플레이어와 전투한 후 7초 동안 열 수 없습니다.
    final boolean pvp_timer_enabled = false;
    // 다시 열기까지의 대기 시간입니다. (밀리초)
    final int shulker_open_cooldown = 100;
    final String defaultname = "셜커 상자";
    // true이면 웅크리기 키를 누르고 우클릭해야 합니다.
    final boolean shiftclicktoopen = false;
    final float volume = 1.0f;
    // true이면 닫은 뒤 이전 인벤토리를 엽니다.
    final boolean openpreviousinv = false;
}
