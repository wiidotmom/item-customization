$item modify entity @s weapon.mainhand {function:"minecraft:set_components",components:{custom_data:{igalaxy_item_customization:{item_settings:$(item_settings)}}}}
$item modify entity @s weapon.mainhand {function:"minecraft:set_components",components:{custom_model_data:$(custom_model_data)}}

function igalaxy_item_customization:template/settings/reset_triggers