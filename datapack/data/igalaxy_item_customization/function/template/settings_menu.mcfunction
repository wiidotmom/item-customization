tellraw @s {"color":"white","bold":true,"text": "Item Customization Settings"}
tellraw @s [{"color":"white","text":" Item Model: "},{"click_event":{"action":"run_command","command":"/trigger igy_itmcst_item_model"},"color":"light_purple","hover_event":{"action":"show_text","value":[{"text":"This will set the minecraft:item_model component to the name of your offhand item, which should be a resource location."}]},"text":"[Copy from Offhand Name]"}]
tellraw @s {"color":"white","text":" Custom Model Data:"}
tellraw @s [{"color":"white","text":" │ Append String: "},{"click_event":{"action":"run_command","command":"/trigger igy_itmcst_custom_model_data_string"},"color":"light_purple","hover_event":{"action":"show_text","value":[{"text":"This will append the name of your offhand item to the list of strings in the minecraft:custom_model_data component."}]},"text":"[Copy from Offhand Name]"}]
tellraw @s [{"color":"white","text":" │ Append Flag: "},{"click_event":{"action":"run_command","command":"/trigger igy_itmcst_custom_model_data_flag_true"},"color":"light_purple","hover_event":{"action":"show_text","value":[{"text":"This will append TRUE (1) to the list of booleans in the minecraft:custom_model_data component."}]},"text":"[TRUE] "},{"click_event":{"action":"run_command","command":"/trigger igy_itmcst_custom_model_data_flag_false"},"color":"light_purple","hover_event":{"action":"show_text","value":[{"text":"This will append FALSE (0) to the list of booleans in the minecraft:custom_model_data component."}]},"text":"[FALSE]"}]
tellraw @s [{"color":"white","text":" │ Append Decimal Color: \n │  "},{"color":"gray","text":"/trigger custom_model_data_decimal_color set <integer>","click_event":{"action":"suggest_command","command":"/trigger custom_model_data_decimal_color set "},"hover_event":{"action":"show_text","value":[{"text":"This will append a decimal color to the list of colors in the minecraft:custom_model_data component."}]}}]
tellraw @s [{"color":"white","text":" │ Append Float: \n └ "},{"color":"gray","text":"/trigger custom_model_data_float set <integer>","click_event":{"action":"suggest_command","command":"/trigger custom_model_data_float set "},"hover_event":{"action":"show_text","value":[{"text":"This will append an integer converted to a float to the list of floats in the minecraft:custom_model_data component."}]}}]
tellraw @s {"color":"white","text":" Equipment:"}
tellraw @s [{"color":"white","text":" │ Texture: "},{"click_event":{"action":"run_command","command":"/trigger igy_itmcst_equipment_texture"},"color":"light_purple","hover_event":{"action":"show_text","value":[{"text":"This will set 'minecraft:equippable'.asset_id to the name of your offhand item, which should be a resource location. This only affects player armor, wolf armor, horse armor, llama carpets, and elytra."}]},"text":"[Copy from Offhand Name]"}]
tellraw @s [{"color":"white","text":" └ Camera Overlay: "},{"click_event":{"action":"run_command","command":"/trigger igy_itmcst_camera_overlay"},"color":"light_purple","hover_event":{"action":"show_text","value":[{"text":"This will set 'minecraft:equippable'.camera_overlay to the name of your offhand item, which should be a resource location. This only affects when the item is worn on your head. The item must already be head-equippable, e.g. helmets or carved pumpkins."}]},"text":"[Copy from Offhand Name]"}]
tellraw @s {"color":"white","text":" Sounds & Songs:"}
tellraw @s [{"color":"white","text":" │ Note Block: "},{"click_event":{"action":"run_command","command":"/trigger igy_itmcst_note_block_sound"},"color":"light_purple","hover_event":{"action":"show_text","value":[{"text":"This will set the minecraft:note_block_sound component to the name of your offhand item, which should be a sound ID. This only affects player heads."}]},"text":"[Copy from Offhand Name]"}]
tellraw @s [{"color":"white","text":" │ Jukebox: "},{"click_event":{"action":"run_command","command":"/trigger igy_itmcst_jukebox_song"},"color":"light_purple","hover_event":{"action":"show_text","value":[{"text":"This will set the minecraft:jukebox_song component to the name of your offhand item, which should be a resource location. This only affects music discs."}]},"text":"[Copy from Offhand Name]"}]
tellraw @s [{"color":"white","text":" └Instrument: "},{"click_event":{"action":"run_command","command":"/trigger igy_itmcst_instrument"},"color":"light_purple","hover_event":{"action":"show_text","value":[{"text":"This will set the minecraft:instrument component to the name of your offhand item, which should be a resource location. This only affects goat horns."}]},"text":"[Copy from Offhand Name]"}]
tellraw @s {"color":"white","text":" Tooltip:"}
tellraw @s [{"color":"white","text":" │ Style: "},{"click_event":{"action":"run_command","command":"/trigger igy_itmcst_tooltip_style"},"color":"light_purple","hover_event":{"action":"show_text","value":[{"text":"This will set the minecraft:tooltip_style component to the name of your offhand item, which should be a resource location."}]},"text":"[Copy from Offhand Name]"}]
tellraw @s [{"color":"white","text":" └Hide: "},{"click_event":{"action":"run_command","command":"/trigger igy_itmcst_hide_additional_tooltip"},"color":"light_purple","hover_event":{"action":"show_text","value":[{"text":"This will add the minecraft:hide_additional_tooltip component, hiding the default lore."}]},"text":"[Additional Text] "},{"click_event":{"action":"run_command","command":"/trigger igy_itmcst_hide_tooltip"},"color":"light_purple","hover_event":{"action":"show_text","value":[{"text":"This will add the minecraft:hide_tooltip component, hiding the entire tooltip, including the name and background."}]},"text":"[Entire Tooltip]"}]
tellraw @s [{"color":"white","text":" Settings: "},{"click_event":{"action":"run_command","command":"/trigger igy_itmcst_copy"},"color":"blue","hover_event":{"action":"show_text","value":[{"text":"This will copy the settings from the Item Customization smithing template in your offhand."}]},"text":"[Copy] "},{"click_event":{"action":"run_command","command":"/trigger igy_itmcst_reset"},"color":"red","hover_event":{"action":"show_text","value":[{"text":"This will reset the settings of your held Item Customization smithing template."}]},"text":"[Reset] "},{"color":"gray","text":"Apply?","hover_event":{"action":"show_text","value":{"text":"To apply the Item Customization smithing template, hold the single desired item in your mainhand and the template in your offhand and interact with a Smithing Table. You must have at least 1 Echo Shard in your inventory. You cannot be holding more than 1 item to apply the template to."}}},{"color":"gray","text":" Duplicate?","hover_event":{"action":"show_text","value":{"text":"To duplicate the Item Customization smithing template, hold the template in your mainhand and 7 Diamonds in your offhand and interact with a Crafting Table. You must have at least 1 Terracotta in your inventory. The settings will be copied to the duplicate template."}}}]