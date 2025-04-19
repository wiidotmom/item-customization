data modify storage igalaxy_item_customization:storage item_settings set from entity @s equipment.offhand.components.'minecraft:custom_data'.item_settings
execute if data entity @s equipment.offhand.components.'minecraft:custom_model_data' run data modify storage igalaxy_item_customization:storage custom_model_data set from entity @s equipment.offhand.components.'minecraft:custom_model_data'
execute unless data entity @s equipment.offhand.components.'minecraft:custom_model_data' run data modify storage igalaxy_item_customization:storage custom_model_data set value {}

execute store result score @s igy_itmcst_offhand_count run data get entity @s equipment.offhand.count
scoreboard players remove @s igy_itmcst_offhand_count 1
execute store result storage igalaxy_item_customization:storage offhand_count int 1 run scoreboard players get @s igy_itmcst_offhand_count
scoreboard players reset @s igy_itmcst_offhand_count

function igalaxy_item_customization:smithing/apply_settings with storage igalaxy_item_customization:storage