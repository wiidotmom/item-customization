$item modify entity @s weapon.mainhand {function:"set_components",components:{custom_data:{item_settings:$(item_settings)}}}
$tellraw @s {"text":"copied settings: $(item_settings)"}