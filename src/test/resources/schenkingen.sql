insert into schenkingen(vanMensId, aanMensId, wanneer, bedrag)
values
    ((select id from mensen where naam='test2'), (select id from mensen where naam='test3'), '2023/01/01', 100),
    ((select id from mensen where naam='test2'), (select id from mensen where naam='test3'), '2023/02/02', 200);