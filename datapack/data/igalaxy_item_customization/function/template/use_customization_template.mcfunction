advancement revoke @s only igalaxy_item_customization:use_customization_template
execute unless entity @s[predicate=igalaxy_item_customization:mainhand_customization_template] run return run return fail

scoreboard players set @s igy_itmcst_use_template_cooldown 20

function igalaxy_item_customization:template/settings_menu
playsound minecraft:ui.button.click master @s ~ ~ ~ 0.25 1