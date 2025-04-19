execute store result score @s igy_itmcst_ingredient_count run clear @s terracotta 0
execute unless score @s igy_itmcst_ingredient_count matches 1.. run return fail
clear @s terracotta 1
clear @s gold_ingot 7
function igalaxy_item_customization:template/give