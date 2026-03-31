package dev.syoritohatsuki.fstatsapi.client.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Map;

import static java.util.Map.entry;

public final class TextsWithFallbacks {
    /*   Duplication of english language file. Require for FabricAPI free projects   */
    public static Map<String, String> fallbacks = Map.ofEntries(
        entry("chat.modname", "fStats"),
        entry("chat.mail.warning", "Never send mail to people that you don't trust!"),
        entry("chat.mail.open", "Open in Mail App"),
        entry("chat.mail.confirmTrusted", "Do you want to open this mail or copy it to your clipboard?"),
        entry("chat.mail.confirm", "Are you sure you want to open the following mail?"),
        entry("fstats_info.screen.title", "fStats Data Collection"),
        entry("fstats_info.screen.description", "Collecting this data helps developer improve their mods by guiding them in directions that are relevant to players"),
        entry("fstats_info.button.contact_developer", "Contact Developer"),
        entry("fstats_info.button.official_page", "Official Page"),
        entry("fstats_info.button.collect_mode", "Collect"),
        entry("fstats.event.world", "After entering world"),
        entry("fstats.event.world.description", "Next data will be collected anonymously after you enter world"),
        entry("fstats.collect.country_code", "Country Code"),
        entry("fstats.collect.fabric_api_version", "Fabric API Version"),
        entry("fstats.collect.minecraft_version", "Minecraft Version"),
        entry("fstats.collect.mod_version", "Mod Version"),
        entry("fstats.collect.os", "Operation System"),
        entry("fstats.collect.if_allowed", "If allowed"),
        entry("fstats.collect.if_installed", "If installed"),
        entry("fstats.collect.first_letter", "First letter"),
        entry("fstats.mods_use", "List of mods that use fStats")
    );

    public static final MutableComponent MOD_NAME_TEXT = getTextOrFallback("chat.modname");
    public static final MutableComponent MAIL_WARNING_TEXT = getTextOrFallback("chat.mail.warning");
    public static final MutableComponent MAIL_OPEN_TEXT = getTextOrFallback("chat.mail.open");
    public static final MutableComponent MAIL_CONFIRM_TRUSTED_TEXT = getTextOrFallback("chat.mail.confirmTrusted");
    public static final MutableComponent MAIL_CONFIRM_TEXT = getTextOrFallback("chat.mail.confirm");
    public static final MutableComponent TITLE_TEXT = getTextOrFallback("fstats_info.screen.title");
    public static final MutableComponent DESCRIPTION_TEXT = getTextOrFallback("fstats_info.screen.description");
    public static final MutableComponent CONTACT_DEVELOPER_TEXT = getTextOrFallback("fstats_info.button.contact_developer");
    public static final MutableComponent OFFICIAL_PAGE_TEXT = getTextOrFallback("fstats_info.button.official_page");
    public static final MutableComponent COLLECT_MODE_TEXT = getTextOrFallback("fstats_info.button.collect_mode");
    public static final MutableComponent WORLD_EVENT_TEXT = getTextOrFallback("fstats.event.world");
    public static final MutableComponent WORLD_EVENT_DESCRIPTION_TEXT = getTextOrFallback("fstats.event.world.description");
    public static final MutableComponent COUNTRY_CODE_TEXT = getTextOrFallback("fstats.collect.country_code");
    public static final MutableComponent FABRIC_API_VERSION_TEXT = getTextOrFallback("fstats.collect.fabric_api_version");
    public static final MutableComponent MOD_VERSION_TEXT = getTextOrFallback("fstats.collect.mod_version");
    public static final MutableComponent MINECRAFT_VERSION_TEXT = getTextOrFallback("fstats.collect.minecraft_version");
    public static final MutableComponent OPERATION_SYSTEM_TEXT = getTextOrFallback("fstats.collect.os");
    public static final MutableComponent IF_ALLOWED_TEXT = getTextOrFallback("fstats.collect.if_allowed");
    public static final MutableComponent IF_INSTALLED_TEXT = getTextOrFallback("fstats.collect.if_installed");
    public static final MutableComponent FIRST_LETTER_TEXT = getTextOrFallback("fstats.collect.first_letter");
    public static final MutableComponent MODS_USE_TEXT = getTextOrFallback("fstats.mods_use");

    private static MutableComponent getTextOrFallback(String key) {
        return Component.translatableWithFallback(key, TextsWithFallbacks.fallbacks.get(key));
    }
}
