--DELETE FROM ad_authority WHERE object_rrn IN (6004,600401,600402,600403,6005,600501,600502,600503);

--DELETE FROM ad_editor WHERE object_rrn IN (18,19);

--DELETE FROM ad_table WHERE object_rrn IN (26783274,26783275);

--DELETE FROM ad_tab WHERE object_rrn IN(26783318,26783341,26783342);

--DELETE FROM ad_field WHERE object_rrn IN (26783319,26783321,26783343,26783344,26783347);

--DELETE FROM ad_reftable WHERE object_rrn = 267833

INSERT INTO ad_tab VALUES (26783541,0,'Y',To_Date('2012-03-28','yyyy-mm-dd'),1,To_Date('2012-03-28','yyyy-mm-dd'),1,
  1,'AddEquipment','添加设备', 38721 ,2 ,2 , 'AddEquipment','添加设备');

-- 动态参考表配置 EquipmentList
INSERT INTO ad_reftable VALUES(26783542,0,'Y',To_Date('2012-03-28','yyyy-mm-dd'),1,To_Date('2012-03-28','yyyy-mm-dd'),1,1,
  'EquipmentList','设备列表',26783275,'objectRrn','equipmentName',NULL,NULL);

--动态栏位配置 EquipmentList
INSERT INTO ad_field VALUES( 26783543,0,'Y',To_Date('2012-03-28','yyyy-mm-dd'),1,To_Date('2012-03-28','yyyy-mm-dd'),1,
  2,'equipments','添加设备',38721 , 26783541 ,1 , NULL,'Y','N','N','N','N','Y','Y','N','N',NULL,'duallist',NULL,
  NULL,NULL,NULL,NULL,26783542,NULL,' Equipment Add','添加设备','N',NULL,'N');

--将设备及模具的显示类型改为下拉框
UPDATE ad_field SET DISPLAY_TYPE ='combo' WHERE  object_rrn IN(2379103,17855795);



