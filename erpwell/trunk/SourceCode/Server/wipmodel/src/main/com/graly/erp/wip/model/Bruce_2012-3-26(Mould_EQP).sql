--DELETE FROM ad_authority WHERE object_rrn IN (6004,600401,600402,600403,6005,600501,600502,600503);

--DELETE FROM ad_editor WHERE object_rrn IN (18,19);

--DELETE FROM ad_table WHERE object_rrn IN (26783274,26783275);

--DELETE FROM ad_tab WHERE object_rrn IN(26783318,26783341,26783342);

--DELETE FROM ad_field WHERE object_rrn IN (26783319,26783321,26783343,26783344,26783347);

--DELETE FROM ad_reftable WHERE object_rrn = 267833

--配置模具管理form及按钮
INSERT INTO ad_authority VALUES(
  6004,0,'Y',To_Date('2012-03-26','yyyy-mm-dd'),1,To_Date('2012-03-26','yyyy-mm-dd'),1,0,
  'PRD.Mould','Mould Manager','F','E','18','60','40','Mould Manager','模具管理');
INSERT INTO ad_authority VALUES(
  600401,0,'Y',To_Date('2012-03-26','yyyy-mm-dd'),1,To_Date('2012-03-26','yyyy-mm-dd'),1,0,
  'PRD.Mould.New','Mould New Item','B',NULL,NULL,6004,10,'New','新建');
INSERT INTO ad_authority VALUES(
  600402,0,'Y',To_Date('2012-03-26','yyyy-mm-dd'),1,To_Date('2012-03-26','yyyy-mm-dd'),1,0,
  'PRD.Mould.Save','Mould Save Item','B',NULL,NULL,6004,20,'Save','保存');
INSERT INTO ad_authority VALUES(
  600403,0,'Y',To_Date('2012-03-26','yyyy-mm-dd'),1,To_Date('2012-03-26','yyyy-mm-dd'),1,0,
  'PRD.Mould.Delete','Mould Delete Item','B',NULL,NULL,6004,30,'Delete','删除');

--配置设备管理form及按钮
INSERT INTO ad_authority VALUES(
  6005,0,'Y',To_Date('2012-03-26','yyyy-mm-dd'),1,To_Date('2012-03-26','yyyy-mm-dd'),1,0,
  'PRD.Equipment','Equipment Manager','F','E','19','60','50','Equipment Manager','设备管理');
INSERT INTO ad_authority VALUES(
  600501,0,'Y',To_Date('2012-03-26','yyyy-mm-dd'),1,To_Date('2012-03-26','yyyy-mm-dd'),1,0,
  'PRD.Equipment.New','Equipment New Item','B',NULL,NULL,6005,10,'New','新建');
INSERT INTO ad_authority VALUES(
  600502,0,'Y',To_Date('2012-03-26','yyyy-mm-dd'),1,To_Date('2012-03-26','yyyy-mm-dd'),1,0,
  'PRD.Equipment.Save','Equipment Save Item','B',NULL,NULL,6005,20,'Save','保存');
INSERT INTO ad_authority VALUES(
  600503,0,'Y',To_Date('2012-03-26','yyyy-mm-dd'),1,To_Date('2012-03-26','yyyy-mm-dd'),1,0,
  'PRD.Equipment.Delete','Equipment Delete Item','B',NULL,NULL,6005,30,'Delete','删除');


INSERT INTO ad_editor VALUES(18,0,'Y',To_Date('2012-03-26','yyyy-mm-dd'),1,To_Date('2012-03-26','yyyy-mm-dd'),
  1,0,'EntityEditor',NULL, 'com.graly.framework.base.entitymanager.editor.EntityEditor',26783274,NULL,NULL,NULL,NULL) ;

INSERT INTO ad_editor VALUES(19,0,'Y',To_Date('2012-03-26','yyyy-mm-dd'),1,To_Date('2012-03-26','yyyy-mm-dd'),
  1,0,'EntityEditor',NULL, 'com.graly.framework.base.entitymanager.editor.EntityEditor',26783275,NULL,NULL,NULL,NULL);


insert into ad_table values('26783274',0,'Y',	To_Date('2012-03-26','yyyy-mm-dd'),	1,To_Date('2012-03-26','yyyy-mm-dd'),
  1,1,'WIPMould','模具管理',	'WIP_MOULD',	'N','WIPMould','com.graly.framework.security.model.WIPMould',NULL,NULL,
  'Wip Mould', '模具管理','N',	NULL);
insert into ad_table values('26783275',0,'Y',	To_Date('2012-03-26','yyyy-mm-dd'),	1,To_Date('2012-03-26','yyyy-mm-dd'),
  1,1,'WIPEquipment','设备管理','WIP_EQUIPMENT','N','WIPEquipment','com.graly.framework.security.model.WIPEquipment',NULL,NULL,
  'Wip Equipment','设备管理','N',NULL);

INSERT INTO ad_tab VALUES (26783318,0,'Y',To_Date('2012-03-26','yyyy-mm-dd'),1,To_Date('2012-03-26','yyyy-mm-dd'),1,
  1,'BasicInfo','基本信息', 26783274 ,1 ,2 , 'BasicInfo','基本信息');

INSERT INTO ad_tab VALUES (26783319,0,'Y',To_Date('2012-04-10','yyyy-mm-dd'),1,To_Date('2012-04-10','yyyy-mm-dd'),1,
  1,'MouldHistory','维护历史', 26783274 ,2 ,2 , 'MouldHistory','维护历史');
  
INSERT INTO ad_tab VALUES (26783341,0,'Y',To_Date('2012-03-26','yyyy-mm-dd'),1,To_Date('2012-03-26','yyyy-mm-dd'),1,
  1,'BasicInfo','基本信息', 26783275 ,1 ,2 , 'BasicInfo','基本信息');

INSERT INTO ad_tab VALUES (26783342,0,'Y',To_Date('2012-03-26','yyyy-mm-dd'),1,To_Date('2012-03-26','yyyy-mm-dd'),1,
  1,'AddMould','增加模具', 26783275 ,2 ,2 , 'AddMould','增加模具');


INSERT INTO ad_field VALUES( 26783319,0,'Y',To_Date('2012-03-26','yyyy-mm-dd'),1,To_Date('2012-03-26','yyyy-mm-dd'),1,
  4,'mouldName','模具名称',26783274 , 26783318 , 2 , NULL,'Y','Y','Y','Y','N','N','Y','Y','N',NULL,'text','string',
  NULL,NULL,NULL,NULL,NULL,NULL,'Mould Name','模具名称','N',NULL,'N');

INSERT INTO ad_field VALUES( 26783321,0,'Y',To_Date('2012-03-26','yyyy-mm-dd'),1,To_Date('2012-03-26','yyyy-mm-dd'),1,
  9,'mouldId','模具ID',26783274 , 26783318 , 1 , NULL,'Y','Y','Y','Y','N','N','Y','Y','N',NULL,'text','string',
  NULL,NULL,NULL,NULL,NULL,NULL,'Mould ID','模具ID','N',NULL,'N');

--INSERT INTO ad_field VALUES( 26783322,0,'Y',To_Date('2012-04-10','yyyy-mm-dd'),1,To_Date('2012-04-10','yyyy-mm-dd'),1,
--  10,'maintenance','维护信息',26783274 , 26783319 , 1 , NULL,'Y','N','N','N','N','Y','Y','N','N',NULL,'text','string',
--  NULL,NULL,NULL,NULL,NULL,NULL,'New Mould History','维护信息','N',NULL,'N');

--INSERT INTO ad_field VALUES( 26783323,0,'Y',To_Date('2012-03-26','yyyy-mm-dd'),1,To_Date('2012-03-26','yyyy-mm-dd'),1,
--  11,'maintenanceHis','维护历史',26783274 , 26783319 , 2 , NULL,'Y','N','N','N','Y','N','Y','N','N',NULL,'textarea','string',
--  NULL,NULL,NULL,NULL,NULL,NULL,'Mould History','维护历史','N',NULL,'N');

INSERT INTO ad_field VALUES( 26783343,0,'Y',To_Date('2012-03-26','yyyy-mm-dd'),1,To_Date('2012-03-26','yyyy-mm-dd'),1,
  3,'equipmentId','设备ID',26783275 , 26783341 , 1 , NULL,'Y','Y','Y','Y','N','N','Y','Y','N',NULL,'text','string',
  NULL,NULL,NULL,NULL,NULL,NULL,'Equipment Id','设备ID','N',NULL,'N');

INSERT INTO ad_field VALUES( 26783344,0,'Y',To_Date('2012-03-26','yyyy-mm-dd'),1,To_Date('2012-03-26','yyyy-mm-dd'),1,
  2,'equipmentName','设备名称',26783275 , 26783341 , 2 , NULL,'Y','Y','Y','Y','N','N','Y','Y','N',NULL,'text','string',
  NULL,NULL,NULL,NULL,NULL,NULL,'Equipment Name','设备名称','N',NULL,'N');

-- 动态参考表配置 MouldList
INSERT INTO ad_reftable VALUES(26783346,0,'Y',To_Date('2012-03-26','yyyy-mm-dd'),1,To_Date('2012-03-26','yyyy-mm-dd'),1,2,
  'MouldList','模具列表（显示模具名称，保存objectRrn）',26783274,'objectRrn','mouldName',NULL,NULL);
--动态栏位配置 moulds
INSERT INTO ad_field VALUES( 26783347,0,'Y',To_Date('2012-03-26','yyyy-mm-dd'),1,To_Date('2012-03-26','yyyy-mm-dd'),1,
  2,'moulds','添加模具',26783275 , 26783342 ,1 , NULL,'Y','N','N','N','N','Y','Y','N','N',NULL,'duallist',NULL,
  NULL,NULL,NULL,NULL,26783346,NULL,' Mould Add','添加模具','N',NULL,'N');
