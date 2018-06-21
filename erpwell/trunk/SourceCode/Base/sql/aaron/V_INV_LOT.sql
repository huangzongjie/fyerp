create or replace view v_inv_lot as
select b.object_rrn,b.org_rrn,b.is_active,b.created,b.created_by,b.updated,b.updated_by,b.lock_version,b.material_id,e.lot_id,c.doc_id,c.date_created,b.qty_movement from inv_movement_line b,inv_movement c,inv_movement_line_lot d,wip_lot e
where b.movement_rrn=c.object_rrn
and c.doc_status='APPROVED'
and b.object_rrn=d.movement_line_rrn
and d.lot_rrn=e.object_rrn;