echo "Contents of ${envFile}:"
                        cat "${envFile}" | while read line; do
                            if [ ! -z "\${line}" ] && [ "\${line:0:1}" != "#" ]; then
                                echo "\${line}"
                            fi
                        done
