--����һ�����У�materail_name
ALTER TABLE wip_mo_line ADD (material_name VARCHAR2(128));

--�����ӵ����и�ֵ
UPDATE wip_mo_line wip
SET material_name = (SELECT pdm.name
                   FROM pdm_material pdm
                   WHERE pdm.object_rrn = wip.material_rrn)
WHERE EXISTS (SELECT * FROM pdm_material pdm WHERE pdm.object_rrn = wip.material_rrn);

COMMIT;


--��AD_Field����һ���ֶΣ����ڹ����ӹ��������������
INSERT INTO ad_field (OBJECT_RRN,ORG_RRN,IS_ACTIVE,CREATED,CREATED_BY,UPDATED,UPDATED_BY,LOCK_VERSION,
                      NAME,DESCRIPTION,TABLE_RRN,TAB_RRN,SEQ_NO,DISPLAY_LENGTH,IS_DISPLAY,IS_KEY,IS_MAIN,
                      IS_QUERY,IS_READONLY,IS_EDITABLE,IS_SAMELINE,IS_MANDATORY,IS_UPPER,IS_TABLE_FIELD,
                      DISPLAY_TYPE,DATA_TYPE,MIN_VALUE,MAX_VALUE,NAMING_RULE,REFERENCE_NAME,REFTABLE_RRN,
                      USER_REFLIST_NAME,LABEL,LABEL_ZH,IS_PARENT,REFERENCE_RULE,IS_ADVANCE_QUERY)
VALUES ('26664269','0','Y',To_Date('02.03.2012 19:29:27','dd.mm.yyyy HH24:MI:SS'),'1',
         To_Date('03.03.2012 14:37:19','dd.mm.yyyy HH24:MI:SS'),'1','18','materialName',
        '�������ƣ����ڲ�ѯ��','38347','43880','12','','Y','N','N','Y','N','Y','Y',
        'N','N','','text','string','','','','','','','MaterialName','��������','N','','N');

INSERT INTO ad_field (OBJECT_RRN,ORG_RRN,IS_ACTIVE,CREATED,CREATED_BY,UPDATED,UPDATED_BY,LOCK_VERSION,
                      NAME,DESCRIPTION,TABLE_RRN,TAB_RRN,SEQ_NO,DISPLAY_LENGTH,IS_DISPLAY,IS_KEY,IS_MAIN,
                      IS_QUERY,IS_READONLY,IS_EDITABLE,IS_SAMELINE,IS_MANDATORY,IS_UPPER,IS_TABLE_FIELD,
                      DISPLAY_TYPE,DATA_TYPE,MIN_VALUE,MAX_VALUE,NAMING_RULE,REFERENCE_NAME,REFTABLE_RRN,
                      USER_REFLIST_NAME,LABEL,LABEL_ZH,IS_PARENT,REFERENCE_RULE,IS_ADVANCE_QUERY)
VALUES ('26664563','0','Y',To_Date('05.03.2012 19:29:27','dd.mm.yyyy HH24:MI:SS'),'1',
         To_Date('06.03.2012 14:37:19','dd.mm.yyyy HH24:MI:SS'),'1','6','materialName',
        '�������ƣ������½���','38347','43880','12','','Y','N','N','N','Y','N','Y',
        'N','N','','reftext','string','','','','','','','MaterialName','��������','N','materialRrn.name','N');
COMMIT;