-- Add/modify columns 
alter table WIP_MO_LINE add DATE_UNMERGE date;
alter table WIP_MO_LINE add DATE_MERGE date;
alter table WIP_MO_LINE add MERGE_NEW_RRN NUMBER(19);
-- Add comments to the columns 
comment on column WIP_MO_LINE.DATE_UNMERGE
  is 'ȡ���ϲ�ʱ��';
comment on column WIP_MO_LINE.DATE_MERGE
  is '�ϲ�ʱ��';
comment on column WIP_MO_LINE.MERGE_NEW_RRN
  is '�ϲ����ɵ���moLine��objectRrn';
-- Add/modify columns 
alter table WIP_MO_LINE add MERGE_BY NUMBER(19);
-- Add comments to the columns 
comment on column WIP_MO_LINE.MERGE_BY
  is '�ϲ���';
-- Add/modify columns 
alter table WIPHIS_MO_LINE add MERGE_BY NUMBER(19);
-- Add/modify columns 
alter table WIPHIS_MO_LINE add DATE_MERGE date;
-- Add/modify columns 
alter table WIPHIS_MO_LINE add MERGE_NEW_RRN NUMBER(19);

