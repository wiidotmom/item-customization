execute if score @s igy_itmcst_item_model matches 1.. run data modify storage igalaxy_item_customization:storage field_name set value "minecraft:item_model"
execute if score @s igy_itmcst_item_model matches 1.. run function igalaxy_item_customization:template/settings/get_string_input

execute if score @s igy_itmcst_custom_model_data_string matches 1.. run function igalaxy_item_customization:template/settings/get_string_input_cmd

execute if score @s igy_itmcst_custom_model_data_flag_true matches 1.. run function igalaxy_item_customization:template/settings/custom_model_data/flag_true
execute if score @s igy_itmcst_custom_model_data_flag_false matches 1.. run function igalaxy_item_customization:template/settings/custom_model_data/flag_false

execute if score @s custom_model_data_decimal_color matches 1.. store result storage igalaxy_item_customization:storage number_input int 1 run scoreboard players get @s custom_model_data_decimal_color
execute if score @s custom_model_data_decimal_color matches 1.. run function igalaxy_item_customization:template/settings/custom_model_data/decimal_color with storage igalaxy_item_customization:storage

execute if score @s custom_model_data_float matches 1.. store result storage igalaxy_item_customization:storage number_input float 1 run scoreboard players get @s custom_model_data_float
execute if score @s custom_model_data_float matches 1.. run function igalaxy_item_customization:template/settings/custom_model_data/float with storage igalaxy_item_customization:storage

execute if score @s igy_itmcst_equipment_texture matches 1.. run function igalaxy_item_customization:template/settings/equippable/get_string_input

execute if score @s igy_itmcst_camera_overlay matches 1.. run function igalaxy_item_customization:template/settings/equippable/camera_overlay/get_string_input

execute if score @s igy_itmcst_note_block_sound matches 1.. run data modify storage igalaxy_item_customization:storage field_name set value "minecraft:note_block_sound"
execute if score @s igy_itmcst_note_block_sound matches 1.. run function igalaxy_item_customization:template/settings/get_string_input

execute if score @s igy_itmcst_jukebox_song matches 1.. run data modify storage igalaxy_item_customization:storage field_name set value "minecraft:jukebox_playable"
execute if score @s igy_itmcst_jukebox_song matches 1.. run function igalaxy_item_customization:template/settings/get_string_input

execute if score @s igy_itmcst_instrument matches 1.. run data modify storage igalaxy_item_customization:storage field_name set value "minecraft:instrument"
execute if score @s igy_itmcst_instrument matches 1.. run function igalaxy_item_customization:template/settings/get_string_input

execute if score @s igy_itmcst_tooltip_style matches 1.. run data modify storage igalaxy_item_customization:storage field_name set value "minecraft:tooltip_style"
execute if score @s igy_itmcst_tooltip_style matches 1.. run function igalaxy_item_customization:template/settings/get_string_input

# TODO: TOOLTIP HIDE ADDITIONAl

execute if score @s igy_itmcst_hide_tooltip matches 1.. run function igalaxy_item_customization:template/settings/tooltip/hide_completely

execute if score @s igy_itmcst_copy matches 1.. run data modify storage igalaxy_item_customization:storage item_settings set from entity @s equipment.offhand.components.'minecraft:custom_data'.item_settings
execute if score @s igy_itmcst_copy matches 1.. run data modify storage igalaxy_item_customization:storage custom_model_data set from entity @s equipment.offhand.components.'minecraft:custom_model_data'
execute if score @s igy_itmcst_copy matches 1.. run function igalaxy_item_customization:template/settings/copy with storage igalaxy_item_customization:storage

execute if score @s igy_itmcst_reset matches 1.. run function igalaxy_item_customization:template/settings/reset/ask
# TODO: does this actually reset? need to check
execute if score @s item_customization_i_really_want_to_reset_for_real_seriously matches 1.. run function igalaxy_item_customization:template/settings/reset/confirm