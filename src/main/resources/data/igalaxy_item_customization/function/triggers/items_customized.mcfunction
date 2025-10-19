tellraw @a [{text:""},{selector:"@s"},{text:" is now displaying ",color:"gray"},{text:"Items Customized",color:"gold"}]
execute as @a unless score @s igy_item_customization_items_customized matches 1.. run tellraw @s {text:"You have not customized any items",italic:true,color:"gray"}
execute as @a if score @s igy_item_customization_items_customized matches 1 run tellraw @a [{text:"You have customized ",italic:true,color:"gray"},{score:{name:"@s",objective:"igy_item_customization_items_customized"},italic:true,color:"gold"},{text:" item",italic:true,color:"gray"}]
execute as @a if score @s igy_item_customization_items_customized matches 2.. run tellraw @a [{text:"You have customized ",italic:true,color:"gray"},{score:{name:"@s",objective:"igy_item_customization_items_customized"},italic:true,color:"gold"},{text:" items",italic:true,color:"gray"}]

scoreboard players set @s stats_items_customized 0

scoreboard objectives setdisplay sidebar igy_item_customization_items_customized
schedule function igalaxy_item_customization:triggers/scoreboard_clear 600t