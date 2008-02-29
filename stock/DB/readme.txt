
以下コマンドにて、初期DBを設定します。

stock$ psql -e stock < stock_first_ver0315.dump

また、以下コマンドにて、旧バージョンのDBを更新します。

stock$ psql -f command_ver0315.txt stock

