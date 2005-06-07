#!/bin/sh

# Try some echo setup:
if (echo "testing\c"; echo 1,2,3) | grep c >/dev/null; then
  # Stardent Vistra SVR4 grep lacks -e, says ghazi@caip.rutgers.edu.
  if (echo -n testing; echo 1,2,3) | sed s/-n/xn/ | grep xn >/dev/null; then
    ac_n= ac_c='
' ac_t='	'
  else
    ac_n=-n ac_c= ac_t=
  fi
else
  ac_n= ac_c='\c' ac_t=
fi

less -e README-unix
less -e LICENSE
echo $ac_n "Do you accept the terms of this license? (yes/no): $ac_c"
read accept
if [ "$accept" != "y" ] && [ "$accept" != "Y" ] && [ "$accept" != "yes" ] && [ "$accept" != "YES" ]; then
    exit 1
fi

defaultprefix="/usr/local"

echo $ac_n "Where would you like to install InfoPad? [$defaultprefix]: $ac_c"
read prefix
if [ "$prefix" = "" ]; then
    prefix="$defaultprefix"
fi

mkdir -p $prefix/share/infopad
cp -f infopad.jar $prefix/share/infopad/infopad.jar

echo "#!/bin/sh" > infopad
echo "jarloc=\"$prefix/share/infopad/infopad.jar\"" >> infopad
echo "java -jar \$jarloc" >> infopad
chmod 755 infopad

mkdir -p $prefix/bin
cp -f infopad $prefix/bin/infopad

