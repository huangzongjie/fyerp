-- Add/modify columns 
alter table WIP_MO_LINE add DATE_UNMERGE date;
alter table WIP_MO_LINE add DATE_MERGE date;
alter table WIP_MO_LINE add MERGE_NEW_RRN NUMBER(19);
-- Add comments to the columns 
comment on column WIP_MO_LINE.DATE_UNMERGE
  is '取消合并时间';
comment on column WIP_MO_LINE.DATE_MERGE
  is '合并时间';
comment on column WIP_MO_LINE.MERGE_NEW_RRN
  is '合并生成的新moLine的objectRrn';
-- Add/modify columns 
alter table WIP_MO_LINE add MERGE_BY NUMBER(19);
-- Add comments to the columns 
comment on column WIP_MO_LINE.MERGE_BY
  is '合并人';
-- Add/modify columns 
alter table WIPHIS_MO_LINE add MERGE_BY NUMBER(19);
-- Add/modify columns 
alter table WIPHIS_MO_LINE add DATE_MERGE date;
-- Add/modify columns 
alter table WIPHIS_MO_LINE add MERGE_NEW_RRN NUMBER(19);

