-- Run this in Supabase SQL Editor before deploying the updated CodeHub app.
-- It is written to be safe if some columns/tables already exist.

alter table users
    add column if not exists notify_comments boolean not null default true,
    add column if not exists notify_approvals boolean not null default true,
    add column if not exists notify_product_updates boolean not null default false;

alter table snippets
    add column if not exists collection_name varchar(255),
    add column if not exists share_token varchar(255),
    add column if not exists public_snippet boolean not null default false,
    add column if not exists updated_at timestamp,
    add column if not exists deleted_at timestamp,
    add column if not exists created_by varchar(255),
    add column if not exists updated_by varchar(255),
    add column if not exists deleted boolean not null default false;

create unique index if not exists uk_snippets_share_token
    on snippets (share_token)
    where share_token is not null;

create table if not exists tags (
    id bigserial primary key,
    name varchar(255) not null unique
);

create table if not exists snippet_tags (
    snippet_id bigint not null references snippets(id) on delete cascade,
    tag_id bigint not null references tags(id) on delete cascade,
    primary key (snippet_id, tag_id)
);

create table if not exists comments (
    id bigserial primary key,
    snippet_id bigint not null references snippets(id) on delete cascade,
    user_id bigint not null references users(id) on delete cascade,
    content text not null,
    created_at timestamp default now()
);

create table if not exists bookmarks (
    id bigserial primary key,
    user_id bigint not null references users(id) on delete cascade,
    snippet_id bigint not null references snippets(id) on delete cascade,
    created_at timestamp default now(),
    constraint uk_bookmark_user_snippet unique (user_id, snippet_id)
);

create index if not exists idx_tags_name on tags(name);
create index if not exists idx_comments_snippet_created on comments(snippet_id, created_at desc);
create index if not exists idx_bookmarks_user_created on bookmarks(user_id, created_at desc);
