scoreboard objectives add igy_itmcst_ingredient_cost dummy
scoreboard objectives add igy_itmcst_ingredient_count dummy
scoreboard objectives add igy_itmcst_offhand_count dummy
scoreboard objectives add igy_itmcst_use_template_cooldown dummy

scoreboard objectives add igy_itmcst_item_model trigger "Copy item model from offhand name"
scoreboard objectives add igy_itmcst_custom_model_data_string trigger "Append custom model data string from offhand name"
scoreboard objectives add custom_model_data_float trigger "Append custom model data float"
scoreboard objectives add custom_model_data_decimal_color trigger "Append custom model data decimal color"
scoreboard objectives add igy_itmcst_custom_model_data_flag_false trigger "Append custom model data FALSE (0b) flag"
scoreboard objectives add igy_itmcst_custom_model_data_flag_true trigger "Append custom model data TRUE (1b) flag"
scoreboard objectives add igy_itmcst_equipment_texture trigger "Copy equipment texture from offhand name"
scoreboard objectives add igy_itmcst_camera_overlay trigger "Copy camera overlay from offhand name"
scoreboard objectives add igy_itmcst_note_block_sound trigger "Copy note block sound from offhand name"
scoreboard objectives add igy_itmcst_jukebox_song trigger "Copy jukebox song from offhand name"
scoreboard objectives add igy_itmcst_instrument trigger "Copy instrument from offhand name"
scoreboard objectives add igy_itmcst_tooltip_style trigger "Copy tooltip textures from offhand name"
scoreboard objectives add igy_itmcst_hide_additional_tooltip trigger "Hide additional tooltip"
scoreboard objectives add igy_itmcst_hide_tooltip trigger "Hide tooltip (including name)"
scoreboard objectives add igy_itmcst_reset trigger "Reset settings"
scoreboard objectives add item_customization_i_really_want_to_reset_for_real_seriously trigger "Reset settings (confirmation)"
scoreboard objectives add igy_itmcst_copy trigger "Copy settings from offhand template"

scoreboard objectives add igy_itmcst_use_smithing_table minecraft.custom:minecraft.interact_with_smithing_table
scoreboard objectives add igy_itmcst_use_crafting_table minecraft.custom:minecraft.interact_with_crafting_table

schedule function igalaxy_item_customization:clock 10t

scoreboard players set igy_item_customization_setup igy_datapacks 1