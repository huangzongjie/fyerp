update loader_pdm_material t set t.is_lot_control = 'Y';
update loader_pdm_material t set t.lot_type = 'SERIAL' where t.lot_type = 'S';
update loader_pdm_material t set t.lot_type = 'BATCH' where t.lot_type = 'B';
update loader_pdm_material t set t.lot_type = 'MATERIAL' where t.lot_type = 'M';
update loader_pdm_material t set t.lot_type = 'MATERIAL' where t.lot_type is null;

update loader_pdm_material t set t.process_name = '净水机装配' where t.process_name like '%净水机%';
update loader_pdm_material t set t.process_name = '软水机装配' where t.process_name like '%软水机%';
update loader_pdm_material t set t.process_name = '饮水机装配' where t.process_name like '%饮水%';
update loader_pdm_material t set t.process_name = '控制阀装配' where t.process_name like '%控制阀%' or t.process_name like '%阀车间%';
update loader_pdm_material t set t.process_name = '翻新' where t.process_name like '%翻新%';
update loader_pdm_material t set t.process_name = '虚拟' where t.process_name like '%虚拟%';

客供

9877
9894