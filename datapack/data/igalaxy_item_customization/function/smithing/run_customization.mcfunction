data modify storage igalaxy_item_customization:storage item_settings set from entity @s Inventory[{Slot:-106b}].components.'minecraft:custom_data'.'igalaxy_item_customization:item_settings'

execute store result score @s igy_itmcst_offhand_count run data get entity @s Inventory[{Slot:-106b}].count
scoreboard players remove @s igy_itmcst_offhand_count 1
execute store result storage igalaxy_item_customization:storage offhand_count int 1 run scoreboard players get @s igy_itmcst_offhand_count
scoreboard players reset @s igy_itmcst_offhand_count

function igalaxy_item_customization:smithing/apply_settings with storage igalaxy_item_customization:storage