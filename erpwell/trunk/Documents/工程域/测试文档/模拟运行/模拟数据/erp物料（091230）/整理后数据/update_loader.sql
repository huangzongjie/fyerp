update loader_pdm_material t set t.is_lot_control = 'Y';
update loader_pdm_material t set t.lot_type = 'SERIAL' where t.lot_type = 'S';
update loader_pdm_material t set t.lot_type = 'BATCH' where t.lot_type = 'B';
update loader_pdm_material t set t.lot_type = 'MATERIAL' where t.lot_type = 'M';
update loader_pdm_material t set t.lot_type = 'MATERIAL' where t.lot_type is null;

update loader_pdm_material t set t.process_name = '��ˮ��װ��' where t.process_name like '%��ˮ��%';
update loader_pdm_material t set t.process_name = '��ˮ��װ��' where t.process_name like '%��ˮ��%';
update loader_pdm_material t set t.process_name = '��ˮ��װ��' where t.process_name like '%��ˮ%';
update loader_pdm_material t set t.process_name = '���Ʒ�װ��' where t.process_name like '%���Ʒ�%' or t.process_name like '%������%';
update loader_pdm_material t set t.process_name = '����' where t.process_name like '%����%';
update loader_pdm_material t set t.process_name = '����' where t.process_name like '%����%';

�͹�

9877
9894