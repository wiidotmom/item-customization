execute as @a[predicate=igalaxy_item_customization:can_apply_template] if score @s igy_itmcst_use_smithing_table matches 1.. run function igalaxy_item_customization:smithing/run_customization

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

execute as @a[predicate=igalaxy_item_customization:mainhand_customization_template] run function igalaxy_item_customization:check

schedule function igalaxy_item_customization:clock 10t