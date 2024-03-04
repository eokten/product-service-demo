create sequence if not exists product_seq;

create table if not exists product (
id bigint primary key default nextval('product_seq'),
name text,
description text,
price decimal);
