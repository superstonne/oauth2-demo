

-- insert a default client credentials
insert into oauth_client_details
(client_id, client_secret, scope,
 authorized_grant_types, web_server_redirect_uri)
values
('stone', 'HappyStone', 'user_name,user_image',
'authorization_code,implicit,password', 'http://localhost:9000/callback'
);
