$item modify entity @s weapon.mainhand {function:"minecraft:set_components",components:$(item_settings)}
$execute if data entity @s equipment.offhand.components.'minecraft:custom_model_data' run item modify entity @s weapon.mainhand {function:"minecraft:set_components",components:{custom_model_data:$(custom_model_data)}}
$item modify entity @s weapon.offhand {function:"minecraft:set_count",count:$(offhand_count)}

scoreboard players reset @s igy_itmcst_use_smithing_table