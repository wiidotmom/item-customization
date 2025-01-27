execute as @a if score @s igy_itmcst_use_smithing_table matches 1.. run function igalaxy_item_customization:smithing/check_item

execute as @a if score @s igy_itmcst_use_template_cooldown matches 1.. run scoreboard players remove @s igy_itmcst_use_template_cooldown 10
execute as @a unless score @s igy_itmcst_use_template_cooldown matches 0.. run scoreboard players set @s igy_itmcst_use_template_cooldown 0

scoreboard players enable @a custom_model_data_decimal_color
scoreboard players enable @a custom_model_data_float
scoreboard players enable @a igy_itmcst_custom_model_data_flag_false
scoreboard players enable @a igy_itmcst_custom_model_data_flag_true
scoreboard players enable @a igy_itmcst_custom_model_data_string
scoreboard players enable @a igy_itmcst_custom_model_data_string
scoreboard players enable @a igy_itmcst_equipment_texture
scoreboard players enable @a igy_itmcst_hide_additional_tooltip
scoreboard players enable @a igy_itmcst_hide_tooltip
scoreboard players enable @a igy_itmcst_instrument
scoreboard players enable @a igy_itmcst_item_model
scoreboard players enable @a igy_itmcst_jukebox_song
scoreboard players enable @a igy_itmcst_note_block_sound
scoreboard players enable @a igy_itmcst_reset
scoreboard players enable @a igy_itmcst_tooltip_style
scoreboard players enable @a igy_itmcst_camera_overlay
scoreboard players enable @a igy_itmcst_copy
scoreboard players enable @a item_customization_i_really_want_to_reset_for_real_seriously

execute as @a if score @s igy_itmcst_item_model matches 1.. run data modify storage igalaxy_item_customization:storage field_name set value "minecraft:item_model"
execute as @a if score @s igy_itmcst_item_model matches 1.. run function igalaxy_item_customization:template/settings/get_string_input

# TODO: CUSTOM MODEL DATA VALUES

# TODO: EQUIPPABLE TEXTURE

execute as @a if score @s igy_itmcst_note_block_sound matches 1.. run data modify storage igalaxy_item_customization:storage field_name set value "minecraft:note_block_sound"
execute as @a if score @s igy_itmcst_note_block_sound matches 1.. run function igalaxy_item_customization:template/settings/get_string_input

execute as @a if score @s igy_itmcst_jukebox_song matches 1.. run data modify storage igalaxy_item_customization:storage field_name set value "minecraft:jukebox_song"
execute as @a if score @s igy_itmcst_jukebox_song matches 1.. run function igalaxy_item_customization:template/settings/get_string_input

execute as @a if score @s igy_itmcst_instrument matches 1.. run data modify storage igalaxy_item_customization:storage field_name set value "minecraft:instrument"
execute as @a if score @s igy_itmcst_instrument matches 1.. run function igalaxy_item_customization:template/settings/get_string_input

execute as @a if score @s igy_itmcst_tooltip_style matches 1.. run data modify storage igalaxy_item_customization:storage field_name set value "minecraft:tooltip_style"
execute as @a if score @s igy_itmcst_tooltip_style matches 1.. run function igalaxy_item_customization:template/settings/get_string_input

# TODO: TOOLTIP HIDE ADDITIONAl, HIDE COMPLETELY

execute as @a if score @s igy_itmcst_copy matches 1.. run data modify storage igalaxy_item_customization:storage item_settings set from entity @s Inventory[{Slot:-106b}].components.'minecraft:custom_data'.item_settings
execute as @a if score @s igy_itmcst_copy matches 1.. run function igalaxy_item_customization:template/settings/copy with storage igalaxy_item_customization:storage

execute as @a if score @s igy_itmcst_reset matches 1.. run function igalaxy_item_customization:template/settings/reset/ask
# TODO: does this actually reset? need to check
execute as @a if score @s item_customization_i_really_want_to_reset_for_real_seriously matches 1.. run function igalaxy_item_customization:template/settings/reset/confirm

schedule function igalaxy_item_customization:clock 10t