$clear @s resin_clump $(ingredient_cost)
execute at @s run playsound minecraft:block.smithing_table.use master @s ~ ~ ~ 0.25 1
execute store result score @s igy_itmcst_offhand_count run data get entity @s equipment.offhand.count
scoreboard players remove @s igy_itmcst_offhand_count 1
execute store result storage igalaxy_item_customization:storage offhand_count int 1 run scoreboard players get @s igy_itmcst_offhand_count
scoreboard players reset @s igy_itmcst_offhand_count

function igalaxy_item_customization:smithing/apply_settings with storage igalaxy_item_customization:storage