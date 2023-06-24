# https://www.zhangbj.com/p/1437.html
# -r 代表递归
#
git filter-branch --force --index-filter 'git rm -r --cached --ignore-unmatch chat2db-client/static' --prune-empty --tag-name-filter cat -- --all
git push origin developing  --force
git push origin delete_git  --force
