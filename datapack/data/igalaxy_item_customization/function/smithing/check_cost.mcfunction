data remove storage igalaxy_item_customization:storage item_settings
data remove storage igalaxy_item_customization:storage custom_model_data
data modify storage igalaxy_item_customization:storage item_settings set from entity @s equipment.offhand.components.'minecraft:custom_data'.item_settings
execute if data entity @s equipment.offhand.components.'minecraft:custom_model_data' run data modify storage igalaxy_item_customization:storage custom_model_data set from entity @s equipment.offhand.components.'minecraft:custom_model_data'
execute unless data entity @s equipment.offhand.components.'minecraft:custom_model_data' run data modify storage igalaxy_item_customization:storage custom_model_data set value {}

scoreboard players set @s igy_itmcst_ingredient_cost 0
execute if data storage igalaxy_item_customization:storage item_settings.item_model run scoreboard players add @s igy_itmcst_ingredient_cost 1
execute if data storage igalaxy_item_customization:storage item_settings.equippable.asset_id run scoreboard players add @s igy_itmcst_ingredient_cost 6
execute if data storage igalaxy_item_customization:storage item_settings.equippable.camera_overlay run scoreboard players add @s igy_itmcst_ingredient_cost 2
execute if data storage igalaxy_item_customization:storage item_settings.note_block_sound run scoreboard players add @s igy_itmcst_ingredient_cost 1
execute if data storage igalaxy_item_customization:storage item_settings.jukebox_playable run scoreboard players add @s igy_itmcst_ingredient_cost 6
execute if data storage igalaxy_item_customization:storage item_settings.instrument run scoreboard players add @s igy_itmcst_ingredient_cost 6
execute if data storage igalaxy_item_customization:storage item_settings.tooltip_style run scoreboard players add @s igy_itmcst_ingredient_cost 2
execute if data storage igalaxy_item_customization:storage item_settings.tooltip_display run scoreboard players add @s igy_itmcst_ingredient_cost 3

execute store result score @s igy_itmcst_ingredient_count run clear @s minecraft:resin_clump 0

# tellraw @a [{"score":{name:"@s",objective:"igy_itmcst_ingredient_count"}},{text:" >= "},{score:{name:"@s",objective:"igy_itmcst_ingredient_cost"}}]
execute store result storage igalaxy_item_customization:storage ingredient_cost int 1 run scoreboard players get @s igy_itmcst_ingredient_cost
execute if score @s igy_itmcst_ingredient_count >= @s igy_itmcst_ingredient_cost run function igalaxy_item_customization:smithing/run_customization with storage igalaxy_item_customization:storage
execute unless score @s igy_itmcst_ingredient_count >= @s igy_itmcst_ingredient_cost run function igalaxy_item_customization:smithing/cost_fail
scoreboard players reset @s igy_itmcst_ingredient_cost
scoreboard players reset @s igy_itmcst_ingredient_count