INSERT INTO ad_authority VALUES(
  700520,0,'Y',SYSDATE,1,SYSDATE,1,0,'WIP.WorkCenter.MoDissolve', 'WIP WorkCenter Dissolve MoLine',
  'B',NULL,NULL,7005,76,'Dissovle','撤销合并');
INSERT INTO ad_message VALUES (
  20120303,0,'Y',SYSDATE,1,SYSDATE,1,0,'wip.modissolve','zh','撤销合并');
ALTER TABLE wip_mo_line ADD UNMERGE_BY NUMBER(19,0);
commit;