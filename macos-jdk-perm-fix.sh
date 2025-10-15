find . > paths.txt
<paths.txt xargs -I % xattr -d com.apple.quarantine %
